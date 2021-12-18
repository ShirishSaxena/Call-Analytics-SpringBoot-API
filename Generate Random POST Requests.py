import random
import time
import pyperclip
from datetime import datetime

# Random number generator for Adjetter Media assignment (Java)
numToGenerate = 100000
mobileNoStart = 9

# seconds
MinimumOffsetBetween2Date = 60
MaximumOffsetBetween2Date = 240

# Timestamp range
TimeFormat = '%d-%m-%Y %H:%M:%S'

randomDate_start = "01-01-2021 00:00:00"
randomDate_end = "31-12-2021 23:00:00"

def getRandomNum():
    res = [random.randint(0,9) for i in range(9)]
    res.insert(0, mobileNoStart);
    return sum(d * 10**i for i, d in enumerate(res[::-1]))

def getRandomTime():
    str = ""
    range_start = time.mktime(time.strptime(randomDate_start, TimeFormat))
    range_end = time.mktime(time.strptime(randomDate_end, TimeFormat))
    
    startDate = range_start + random.random() * (range_end - range_start)    
    endDate = startDate + random.uniform(MinimumOffsetBetween2Date, MaximumOffsetBetween2Date)

    startTime = time.strftime(TimeFormat, time.localtime(startDate))
    endTime = time.strftime(TimeFormat, time.localtime(endDate))
    
    str = '''"%s",\n\t\t"endTime": "%s"\n\t}''' %(startTime, endTime)
    return str
    


def generate():
    str = '''\n\t{\n\t\t"number": %d,\n\t\t"startTime": %s''' %(getRandomNum(), getRandomTime())
    
    return str


# Starts here
result = '''['''
for i in range(0, numToGenerate):
    result = result + "%s," %generate()

result = result[ : -1] + "\n]"
pyperclip.copy(result)
print(result)
