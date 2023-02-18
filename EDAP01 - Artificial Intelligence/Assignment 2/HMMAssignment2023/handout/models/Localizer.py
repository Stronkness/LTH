#
# The Localizer binds the models together and controls the update cycle in its "update" method.
#

import numpy as np
import matplotlib.pyplot as plt
# The Localizer is the "controller" for the process. In its update()-method, at the moment a mere skeleton, 
# one cycle of the localisation process should be implemented by you. The return values of update() 
# are assumed to go in exactly this form into the viewer, this means that you should only modify the 
# return-parameters of update() if you do not want to use the graphical interface (or you need to change that too!)
import itertools
import random
import importlib

from models import StateModel,TransitionModel,ObservationModel,RobotSimAndFilter

class Directions:
    SOUTH = 0
    EAST = 1
    NORTH = 2
    WEST = 3

class Localizer:
    def __init__(self, sm):

        self.__sm = sm

        self.__tm = TransitionModel(self.__sm)
        self.__om = ObservationModel(self.__sm)

        # change in initialise in case you want to start out with something else
        # initialise can also be called again, if the filtering is to be reinitialised without a change in grid size
        self.initialise()

    # retrieve the transition model that we are currently working with
    def get_transition_model(self) -> np.array:
        return self.__tm

    # retrieve the observation model that we are currently working with
    def get_observation_model(self) -> np.array:
        return self.__om

    # the current true pose (x, y, h) that should be kept in the local variable __trueState
    def get_current_true_pose(self) -> (int, int, int):
        x, y, h = self.__sm.state_to_pose(self.__trueState)
        return x, y, h

    # the current probability distribution over all states
    def get_current_f_vector(self) -> np.array(float):
        return self.__fVec

    # the current sensor reading (as position in the grid). "Nothing" is expressed as None
    def get_current_reading(self) -> (int, int):
        ret = None
        if self.__sense != None:
            ret = self.__sm.reading_to_position(self.__sense)
        return ret

    # get the currently most likely position, based on single most probable pose
    def most_likely_position(self) -> (int, int):
        return self.__estimate

    ################################### Here you need to really fill in stuff! ##################################
    # if you want to start with something else, change the initialisation here!
    #
    # (re-)initialise for a new run without change of size
    def initialise(self):
        self.__trueState = random.randint(0, self.__sm.get_num_of_states() - 1)
        self.__sense = None
        self.__fVec = np.ones(self.__sm.get_num_of_states()) / (self.__sm.get_num_of_states())
        self.__estimate = self.__sm.state_to_position(np.argmax(self.__fVec))
        self.__rs = RobotSimAndFilter.RobotSim(self.__sm.state_to_pose(self.__trueState))
        self.__HMM = RobotSimAndFilter.HMMFilter(self.__om)
        self.__correct_guesses = 0
        self.__measurements = 0
        self.__total_error = 0
    
    #  Implement the update cycle:
    #  - robot moves one step, generates new state / pose
    #  - sensor produces one reading based on the true state / pose
    #  - filtering approach produces new probability distribution based on
    #  sensor reading, transition and sensor models
    #
    #  [DONE] Add an evaluation in terms of Manhattan distance (average over time) and "hit rate"
    #  you can do that here or in the simulation method of the visualisation, using also the
    #  options of the dashboard to show errors...
    #
    #  Report back to the caller (viewer):
    #  Return
    #  - true if sensor reading was not "nothing", else false,
    #  - AND the three values for the (new) true pose (x, y, h),
    #  - AND the two values for the (current) sensor reading (if not "nothing")
    #  - AND the current estimated position
    #  - AND the error made in this step
    #  - AND the new probability distribution
    #
    def sensor_reading_helper(self, robot_position, position, n):
        possible_x = position[0]
        possible_y = position[1]
        current_x = robot_position[0]
        current_y = robot_position[1]
        # Possible new position for the robot with reading data
        possible_new_x, possible_new_y = current_x + possible_x, current_y + possible_y
        # Get sensor reading of new possible position
        reading = self.__sm.position_to_reading(possible_new_x, possible_new_y)
        # Check amount of circle locations from position_to_reading() and tests with number of possible sensor readings.
        # If number of possible sensor readings is smaller than reading then something is wrong.
        # Also its necessary to check if reading is bigger than 0, as oterwise no reading exists.
        if reading >= 0:
            if self.__sm.get_num_of_readings() - 1 > reading:
                # Get the probability for the sensor to have produced reading "reading" when in state "state",
                # in this case test the probability that the sensor made the reading to the true state of the robot
                probability = self.__om.get_o_reading_state(reading, self.__trueState)
                # Increment n which states a surrounding position of the circle if probability is not zero
                if probability != 0: # could be changes to != 0, also shouldnt it be > 0???
                    n += 1
                return reading, probability, n
        return None, None, n


    def update(self) -> (bool, int, int, int, int, int, int, int, int, np.array(1)):   
        # When trying to use the sensor reading code first instead of update robot position I get a hit rate of 8 %, I wonder why ... 
        
        ########################### Update robot position and get new coordinates
        tsX, tsY, tsH = self.__sm.state_to_pose(self.__trueState)
        valid_headings = []
        # Get current position of robot (true state)
        robot_x, robot_y, _ = self.__sm.state_to_pose(self.__trueState)
        # Take out rows and cols to compare the robots location if its a valid move (e.g can't go beyond a wall)
        rows = self.__sm.get_num_of_rows()
        cols = self.__sm.get_num_of_cols()
        # Check valid possible locations with SOUTH-EAST-NORTH-WEST
        if robot_x + 1 <= rows - 1: valid_headings.append(Directions.SOUTH)
        if robot_y + 1 <= cols - 1: valid_headings.append(Directions.EAST)
        if robot_x - 1 >= 0: valid_headings.append(Directions.NORTH)
        if robot_y - 1 >= 0: valid_headings.append(Directions.WEST)
        # Now create a probability distribution of the valid headings to use the distirbution as
        # a weight for the random next location of the robot
        heading_distribution = [0,0,0,0]
        for direction in valid_headings:
            match direction:
                case Directions.SOUTH:
                    possible_state = self.__sm.pose_to_state(robot_x + 1, robot_y, direction)
                case Directions.EAST:
                    possible_state = self.__sm.pose_to_state(robot_x, robot_y - 1, direction)
                case Directions.NORTH:
                    possible_state = self.__sm.pose_to_state(robot_x - 1, robot_y, direction)
                case Directions.WEST:
                    possible_state = self.__sm.pose_to_state(robot_x, robot_y + 1, direction)
            # Get the probability to go from true state to possible next state
            heading_probability = self.__tm.get_T_ij(self.__trueState, possible_state)
            # Add heading probability to the current direction, direction gets probability zero if its not a valid move
            heading_distribution[direction] = heading_probability
        # Filter out int zeroes as they don't have a valid heading for true state
        heading_distribution = list(filter(lambda x: isinstance(x, float), heading_distribution))
        # Randomized selection of heading choice (from HMM pdf) based on probability distribution (weight from heading_distribution)
        guess = random.choices(valid_headings, weights=heading_distribution)
        # Get the new true position coordinates for the robot based on guess (heading)
        tsX, tsY, tsH = self.__rs.update_position(guess[0]) # Now our function gets to work, returns updated coordinates, see RobotSimAndFilter.py for code
        # Don't forget to update the true state of the robot ...
        self.__trueState = self.__sm.pose_to_state(tsX, tsY, tsH)
        ########################### End of robot position and get new coordinates code
            
        ########################### Get the sensor readings and probabilities
        sensor_reading = {} # Sensor reading with each position showing probability of robot being there
        Ls = [
              (1,-1),(1,0),(1,1),
              (-1,0),(-1,1),(-1,-1),
              (0,-1),(0,1)
             ] # Possible new positions of red in first circle (when in blue)
        Ls_2 = [
                (0,2),(0,-2),
                (-1,-2),(-1,2),
                (-2,0),(-2,-1),(-2,-2),(-2,2),(-2,1),
                (1,-2),(1,2),
                (2,-2),(2,-1),(2,0),(2,1),(2,2),
               ] # Possible new positions or yellow in second circle (when in blue)
        # Variables as stated in sensor reding secion of HMM Assignment details pdf
        n_Ls = 1 # Surrounding positions of first circle
        n_Ls_2 = 1 # Surrounding positions of second circle
        # Gets the current true state of the sensor and inserts the state and its probabilty (from OM) into the dictionary
        # Gets the probability for the sensor to have produced reading when in this state, in this case the true state
        true_state = self.__sm.state_to_reading(self.__trueState)
        sensor_reading[true_state] = self.__om.get_o_reading_state(true_state, self.__trueState)
        # Fetches the current position of the robot (blue) by sending in its true state to SM
        robot_position = self.__sm.state_to_position(self.__trueState)
        # Goes through the different possibilities of the first circle (red in picture in pdf) and secon circle (yellow) to find all possibilities 
        # and store its probability in its position in dictionary.
        # Iterates through Ls and Ls_2 at the sametime to remove duplicate code, maybe better time complexity?
        for l1, l2 in itertools.zip_longest(Ls,Ls_2):
            # Calls the helper function to calculate probability of the possible new location for first and second circle
            if l1 is not None: # Becomes None with zip_longest if no elements is left
                reading, probability, n_Ls = self.sensor_reading_helper(robot_position, l1, n_Ls)
                if reading is not None and probability is not None: # Good reading             
                    sensor_reading[reading] = probability
            if l2 is not None:
                reading, probability, n_Ls_2 = self.sensor_reading_helper(robot_position, l2, n_Ls_2)
                if reading is not None and probability is not None:
                    sensor_reading[reading] = probability
        # Calculate the "nothing" probability and store it in the "nothing" position in dictionary
        nothing = 1-0.1-n_Ls*0.05-n_Ls_2*0.025
        sensor_reading[None] = nothing
        # Prepare self.__sense for HMM forward algorthm
        positions = np.zeros(len(self.__fVec))
        positions[0] = sensor_reading[None]
        for i in range(len(positions)):
            if i in list(sensor_reading.keys()):
                positions[i] = sensor_reading[i]
        # Randomized selection of sensor choice (from HMM pdf) based on probability distribution (weight from positions)
        guess = random.choices(np.arange(len(positions)), weights=positions)
        # No reading or available
        if guess[0] == 0: self.__sense = None
        else: self.__sense = guess[0]
        ############################# End of sensor reading code

        self.__fVec, next_state = self.__HMM.forward_algorithm(self.__tm, self.__sense, self.__fVec)
        self.__estimate = self.__sm.state_to_position(next_state)

        # this block can be kept as is
        ret = False  # in case the sensor reading is "nothing" this is kept...
        srX = -1
        srY = -1
        

        eX, eY = self.__estimate
        if self.__sense != None: # Reading!
            srX, srY = self.__sm.reading_to_position(self.__sense)
            ret = True

            if tsX == eX and tsY == eY: # Correct guess? Increment 1 to keep track of total correct guesses
                self.__correct_guesses += 1

            self.__measurements += 1 # Increment 1 to know how many valdig sensor reading exist i.e measurements
            
        
        # Manhattan Distance as error, takes estimated x and y subtracted by x and y from trueState which origins from StateModel
        # Should be around 2
        error = abs(tsX - eX) + abs(tsY - eY)  
        
        self.__total_error += error 
        
        """
        Hit rate is a metric or measure of business performance traditionally associated with sales. It is defined as the number 
        of sales of a product divided by the number of customers who go online, planned call, or visit a company to find out about the product.
        
        In other words, it is amount of correct guesses / total measurements
        
        Not sure how to use it exactly in this implementation, hence the print out after every update()
        """
        try: # Divison by zero occurence
            hit_rate = self.__correct_guesses / self.__measurements
            avg_error = self.__total_error / self.__measurements
        except:
            hit_rate = 0
            avg_error = 0
        print("hit_rate:", hit_rate)
        print("average error:", avg_error)
        
        # if you use the visualisation (dashboard), this return statement needs to be kept the same
        # or the visualisation needs to be adapted (your own risk!)
        return ret, tsX, tsY, tsH, srX, srY, eX, eY, error, self.__fVec

