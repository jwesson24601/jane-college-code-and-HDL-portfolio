#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct Job{
  int startTime;
  int endTime;
  int weight;
} Job;

typedef struct Event{
  int numJobs;
  Job* allJobs;
} Event;

int compareJobs(const void* a, const void* b) {
  int end1 = ((const Job*)a)->endTime;
  int end2 = ((const Job*)b)->endTime;
  return (end1 > end2) - (end2 > end1);
}

int findIndex(Event* theEvent, int startTime, int startIndex, int endIndex) {
  int AStartIndex;
  int AEndIndex;
  int BStartIndex;
  int BEndIndex;
  
  if (endIndex < startIndex) {
    return endIndex;
  }
  
  if (startIndex == endIndex) {
    if (theEvent->allJobs[startIndex].endTime <= startTime) {
      return startIndex;
    } else {
      while (startIndex >= 0 && theEvent->allJobs[startIndex].endTime > startTime) {
        --startIndex;
      }
      return startIndex;
    }
  }
  
  if ((endIndex-startIndex+1) % 2 == 0) {
    AStartIndex = startIndex;
    AEndIndex = startIndex + ((endIndex-startIndex+1)/2) - 1;
    BStartIndex = startIndex + (endIndex-startIndex+1)/2;
    BEndIndex = endIndex;
  } else {
    AStartIndex = startIndex;
    AEndIndex = startIndex + ((endIndex-startIndex+1)/2);
    BStartIndex = startIndex + ((endIndex-startIndex+1)/2) + 1;
    BEndIndex = endIndex;
  }
  
  if (theEvent->allJobs[BStartIndex].endTime > startTime) {
    return findIndex(theEvent, startTime, AStartIndex, AEndIndex);
  } else { 
    return findIndex(theEvent, startTime, BStartIndex, BEndIndex);
  } 
}

int main(int argc, char *argv[]) {

  int numberOfEvents;
  char str[200];
  char* token;
  
  Event* allEvents = malloc(10000*sizeof(Event));

  fgets(str, 200, stdin);
  numberOfEvents = atoi(str);
  if (numberOfEvents > 10000) {
    printf("The number of instances must be less than or equal to 10000!\n");
    free(allEvents);
    return 1;
  }
  
  for (int i=0; i<numberOfEvents; ++i) {
    int numberOfJobs;
    fgets(str, 200, stdin);
    numberOfJobs = atoi(str);
    allEvents[i].numJobs = numberOfJobs;
    allEvents[i].allJobs = malloc(numberOfJobs*sizeof(Job));
    for (int j=0; j < numberOfJobs; ++j) {
      fgets(str, 200, stdin);
      token = strtok(str, " ");
      allEvents[i].allJobs[j].startTime = atoi(token);
      token = strtok(NULL, " ");
      allEvents[i].allJobs[j].endTime = atoi(token);
      token = strtok(NULL, " ");
      allEvents[i].allJobs[j].weight = atoi(token);
    }
  }

  
  long long int* DPArray;
  
  for (int i=0; i<numberOfEvents; ++i) {
    qsort(allEvents[i].allJobs, allEvents[i].numJobs, sizeof(Job), compareJobs);
    DPArray = malloc((allEvents[i].numJobs+1)*sizeof(long long int));
    DPArray[0] = 0;
    for (int j=1; j<=allEvents[i].numJobs; ++j) {
      int theIndex = findIndex(&(allEvents[i]), allEvents[i].allJobs[j-1].startTime, 0, j-2) + 1;
      if (j == allEvents[i].numJobs) {
      }
        if (DPArray[j-1] > DPArray[theIndex] + allEvents[i].allJobs[j-1].weight) {
          DPArray[j] = DPArray[j-1];
        } else {
          DPArray[j] = DPArray[theIndex] + allEvents[i].allJobs[j-1].weight;
        }
      }
    printf("%lli\n", DPArray[allEvents[i].numJobs]);
    free(DPArray);
  }
  
  
  for (int i=0; i<numberOfEvents; ++i) {
    free(allEvents[i].allJobs);
  }
  free(allEvents);
  return 0;
}

