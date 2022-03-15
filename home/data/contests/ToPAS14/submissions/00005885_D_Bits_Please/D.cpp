#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int data[24],pc[24],i,res2=0;
    float med1=0,med2=0,res=0;
    for(i=0;i<24;i++)
    {
    scanf("%d %d",&data[i],&pc[i]);
    }
    for(i=0;i<24;i++)
    {
    med1=med1+pc[i];
    }
    med1=med1/24;
    med2=(pc[21]+pc[22]+pc[23])/3;
    //cout << med1 << endl;
   // cout << med2 << endl;
    res=(med1+med2)/2;
    res2=res;
    if(res2<res)
    {res=res-1;res2=res;}

    cout << res2 << endl;
    return 0;
}
