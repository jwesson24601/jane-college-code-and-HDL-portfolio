#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct PairOfPoints {
  int p;
  int q;
} PairOfPoints;

typedef struct InterCountInstance {
  int numElem;
  PairOfPoints* theList;
} InterCountInstance;

long long int countAndMergeSort(int AStartIndex, int AEndIndex, int BStartIndex, int BEndIndex, long long int ANumInvs, long long int BNumInvs, InterCountInstance* mainList, int firstSortBoolean) {
  PairOfPoints* mergedList;
  int totalNumElem = ((AEndIndex-AStartIndex+1) + (BEndIndex-BStartIndex+1));
  mergedList = malloc(totalNumElem*sizeof(PairOfPoints));
  long long int numInvs = ANumInvs + BNumInvs;
  int AIndex = AStartIndex;
  int BIndex = BStartIndex;
  
  if (firstSortBoolean) {
    for (int i=0; i<totalNumElem; ++i) {
      if (AIndex > AEndIndex) {
        mergedList[i].p = mainList->theList[BIndex].p;
        mergedList[i].q = mainList->theList[BIndex].q;
        ++BIndex;
      } else if (BIndex <= BEndIndex && mainList->theList[BIndex].p < mainList->theList[AIndex].p) {
        mergedList[i].p = mainList->theList[BIndex].p;
        mergedList[i].q = mainList->theList[BIndex].q;
        numInvs += (AEndIndex - AIndex + 1);
        ++BIndex;
      } else {
        mergedList[i].p = mainList->theList[AIndex].p;
        mergedList[i].q = mainList->theList[AIndex].q;
        ++AIndex;
      }
    }
  } else {
    for (int i=0; i<totalNumElem; ++i) {
      if (AIndex > AEndIndex) {
        mergedList[i].p = mainList->theList[BIndex].p;
        mergedList[i].q = mainList->theList[BIndex].q;
        ++BIndex;
      } else if (BIndex <= BEndIndex && mainList->theList[BIndex].q < mainList->theList[AIndex].q) {
        mergedList[i].p = mainList->theList[BIndex].p;
        mergedList[i].q = mainList->theList[BIndex].q;
        numInvs += (AEndIndex - AIndex + 1);
        ++BIndex;
      } else {
        mergedList[i].p = mainList->theList[AIndex].p;
        mergedList[i].q = mainList->theList[AIndex].q;
        ++AIndex;
      }
    }
  }
  
  for (int i=0; i<totalNumElem; ++i) {
    mainList->theList[AStartIndex+i].p = mergedList[i].p;
    mainList->theList[AStartIndex+i].q = mergedList[i].q;
  }

  free(mergedList);
  return numInvs;
}

long long int countInvAndSort(InterCountInstance* listToSort, int startIndex, int endIndex, int firstSortBoolean) {
  int AStartIndex;
  int AEndIndex;
  int BStartIndex;
  int BEndIndex;
  long long int ANumInvs;
  long long int BNumInvs;
  
  if (startIndex == endIndex) {
    return 0;
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
  
  ANumInvs = countInvAndSort(listToSort, AStartIndex, AEndIndex, firstSortBoolean);
  BNumInvs = countInvAndSort(listToSort, BStartIndex, BEndIndex, firstSortBoolean);
  return countAndMergeSort(AStartIndex, AEndIndex, BStartIndex, BEndIndex, ANumInvs, BNumInvs, listToSort, firstSortBoolean);
}

int main(int argc, char *argv[]) {

  int numberOfReqs;
  char* str;
  
  InterCountInstance* allReqs = malloc(3000*sizeof(InterCountInstance));
  str = malloc(1000*sizeof(char));

  fgets(str, 1000, stdin);
  numberOfReqs = atoi(str);
  if (numberOfReqs > 3000) {
    printf("The number of instances must be less than or equal to 3000!\n");
    free(str);
		free(allReqs);
    return 1;
  }
  
  for (int i=0; i<numberOfReqs; ++i) {
    fgets(str, 1000, stdin);
    allReqs[i].numElem = atoi(str);
    allReqs[i].theList = malloc(allReqs[i].numElem*sizeof(PairOfPoints));
    
    for (int j=0; j<allReqs[i].numElem; ++j) {
      fgets(str, 1000, stdin);
      allReqs[i].theList[j].q = atoi(str);
    }
    
    for (int j=0; j<allReqs[i].numElem; ++j) {
      fgets(str, 1000, stdin);
      allReqs[i].theList[j].p = atoi(str);
    }
  }
  
  for (int i=0; i<numberOfReqs; ++i) {
    countInvAndSort(&(allReqs[i]), 0, allReqs[i].numElem-1, 0);
    printf("%lli\n", countInvAndSort(&(allReqs[i]), 0, allReqs[i].numElem-1, 1));
  }
	
	for (int i=0; i<numberOfReqs; ++i) {
    free(allReqs[i].theList);
  }
  free(allReqs);
  free(str);
  return 0;
}
