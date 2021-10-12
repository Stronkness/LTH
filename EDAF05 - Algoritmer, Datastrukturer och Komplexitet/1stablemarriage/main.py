import timeit
import time


def main():
    preps = time.time()
    T = int(input())
    men = {}
    women = {}
    data = []

    # Snabb lösning för messy-problem. ---

    # Lägg in i listan data
    while True:
        try:
            data.extend(input().strip().split(" "))
        except:
            break;

    # split data till listor storlek T +1

    split_data = [data[x:x + T + 1] for x in range(0, len(data), T + 1)]

    # add to dicts ---
    for d in split_data:
        if d[0] not in women.keys():

            quick_index = [None] * len(d[1:])
            for i, value in enumerate(d[1:], start=1):
                quick_index[int(value) - 1] = i

            women[d[0]] = quick_index
        else:
            men[d[0]] = d[1:]

    # print("--prep-%s seconds ----" % (time.time() - preps))
    match(man=men, wom=women, t=T)


# Match algo
# logiken jag skrev denna på kan nog glömmas
def match(man, wom, t):
    st = time.time()
    pair = {}
    m = man.copy()
    while m:
        # top dict
        first_entry = list(m.items())[0]
        # name @String
        top_man = first_entry[0]
        # prefs @List
        pref_woman = first_entry[1][0]

        # Om K saknar M
        if pref_woman not in pair.keys():
            pair[pref_woman] = top_man

            m.pop(top_man, None)  # samma
        else:
            # Her prefs @List
            wom_pref = wom[pref_woman]

            # if new M is more desirable than current
            if wom_pref[int(top_man) - 1] < wom_pref[int(pair[pref_woman]) - 1]:

                replaced_man = str([pair[pref_woman]][0])

                m[replaced_man] = man[replaced_man]
                m[replaced_man].remove(pref_woman)

                pair[pref_woman] = top_man
                m.pop(top_man, None)  # hade [0]
            else:  # won't happen
                m[top_man].remove(pref_woman)
    # print("--Algo-%s seconds ----" % (time.time() - st))

    for i in range(1, t + 1):
        print(pair[str(i)])


if __name__ == '__main__':
    start_time = time.time()
    main()
    # print("----%s seconds ----" % (time.time() - start_time))