
import random
import numpy as np

from models import TransitionModel,ObservationModel,StateModel

#
# Add your Robot Simulator here
#
class RobotSim:
    def __init__(self, position:(int,int,int)): # Get coordinates of the True state
        self.__x = position[0] # Robot current position in x-axis
        self.__y = position[1] # Robot current position in y-axis
        self.__direction = position[2] # Direction it is pointing to
        print("RobotSim init.")
        
    def update_position(self, direction): # Update and sends back True State
        directions = [ # Based on reversed grid
            (1,0), # SOUTH
            (0,1), # EAST
            (-1,0), # NORTH
            (0,-1) # WEST
        ]
        move_x, move_y = directions[direction] # Matches the direction (0-3) with the coordinate move and takes out coordinates to add to (x,y)
        self.__x += move_x
        self.__y += move_y
        self.__direction = direction # Direction as stated in directions array
        return self.__x, self.__y, self.__direction
        
#
# Add your Filtering approach here (or within the Localiser, that is your choice!)
#
class HMMFilter:
    def __init__(self, om):
        self.__om = om
        print("HHMM Filter init.")

    # Big inspiration from 15.3.1 (Book) pseudo code and Wikipedia page about "Forward algorithm" https://en.wikipedia.org/wiki/Forward_algorithm
    def forward_algorithm(self, TM, sensor_position, prob_distribution): # prob_distribution is the vector f, TM is for transpose of TransitionModle needed for formula
        probability = -1
        next_state = None
        O_matrix = self.__om.get_o_reading(sensor_position) # diagonale matrix O_reading with probabilities of the states i
        
        """
        Calculating the forward equation: f_{t+1} = O_{t+1}*T^T*f_{t}
        by first calculating T^T*f_{t} (temp_distribution)
        and then multiply with O which is the dot product in this case
        giving us the joint_probability
        """
        temp_distribution = np.dot(TM.get_T_transp(), prob_distribution)
        joint_probability = np.dot(O_matrix, temp_distribution) # alpha_{T}(x_{T})
        # Used later when calculating the output of algorithm (conditional probability, see formula for Forward Algorithm), alpha_{T}
        related_joint_probability = np.sum(joint_probability)
        
        # Calculate the conditional probability, p(x_{T} | y_{T}) = alpha_{T}(x_{T}) / alpha_{T} 
        conditional_probability = joint_probability / related_joint_probability
        
        # Choose the highest probability which gives the best guess state where the robot is        
        for i in range(len(joint_probability)):
            if joint_probability[i] > probability:
                probability = joint_probability[i]
                next_state = i
        
        return conditional_probability, next_state # Probability distribution and next state used in StateModel
