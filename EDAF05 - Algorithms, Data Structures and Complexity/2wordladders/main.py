from collections import defaultdict


# Node borta

class Graph:
    # when calling graph it initiates a dict with list as values.

    def __init__(self):
        self.graph = defaultdict(list)

    # makes new key for word if it doesn't exist.
    # appends neighbor to value list
    def addEdge(self, word, neighbor):
        self.graph[word].append(neighbor)

    def bfs(self, start, end):
        if start == end:
            return 0

        q = []
        visited = set()
        previous = {}
        #previous[start] = 0

        q.append(start)
        visited.add(start)
        #depth = 0
        while q:
            curr = q.pop(0)
            #depth +=1
            for neighbor in self.graph[curr]:  # köns första grannar
                if neighbor not in visited:  # om dessa ej blivit besökta, gå dit och lägg tidigare som granne
                    previous[neighbor] = curr

                    if neighbor == end:  # found
                        return self.getPathLength(previous, start, end)

                    q.append(neighbor)
                    visited.add(neighbor)


        return "Impossible"

    def getPathLength(self, previous, start, end):
        prev = previous[end]
        c = 0
        while True:
            c += 1
            if prev == start:
                return c
            prev = previous[prev]



# if the last four letters from s1 exist in s2 then draw path.
# return false if it does not exist
def drawPath(s1, s2):
    s2 = list(s2)
    for letter in s1[1:]:
        if letter not in s2:
            return False
        s2.remove(letter)

    return True


def makeGraph(word_list, g):
    # oklart om Node mest gör det kompliceratet

    prev_word = []
    for curr in word_list:
        for word in prev_word:

            if drawPath(curr, word):  # arc from currword to word. If drawpath condition is OK then add word as neighbor
                g.addEdge(curr, word)
            if drawPath(word, curr):  # arc from currword to word. If drawpath condition is OK then add curr as neighbor
                g.addEdge(word, curr)

        prev_word.append(curr)


def main():
    N, Q = input().split(" ")

    word_list = []

    path_list = []

    # objects
    g = Graph()
    # n = Node()

    # -- Prepare word & paths lists --

    # add words
    for _ in range(int(N)):
        word_list.append(input())
    # add paths
    for _ in range(int(Q)):
        path_list.append(input().split(" "))

    # -- Make graph and results --

    # make graph

    makeGraph(word_list, g)

    # results

    for path in path_list:
        start, end = path
        result = g.bfs(start, end)

        print(result)


if __name__ == '__main__':
    main()