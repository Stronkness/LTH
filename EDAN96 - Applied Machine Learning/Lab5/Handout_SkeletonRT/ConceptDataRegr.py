
class ConceptDataRegr:

    def __init__(self):

        self.attributes = {'wind':['strong', 'weak'], 'airTemp':['warm','cold'], 'humidity':['normal', 'high'],
                  'sky':['sunny', 'cloudy', 'rainy'], 'waterTemp':['warm', 'cool'], 'forecast':['same', 'change']}
        
        self.data = [['strong','warm','normal', 'sunny', 'warm', 'same'],
            ['strong', 'warm', 'high', 'sunny', 'warm', 'same'],
            ['strong', 'cold', 'high', 'rainy', 'warm', 'change'],
            ['strong', 'warm', 'high', 'sunny', 'cool', 'change']]

        self.target = (0.9, 0.8, 0.1, 0.85)

        self.testData = []

        self.testTarget = ()

    def get_data(self):
        return self.attributes, self.data, self.target, self.testData, self.testTarget
