#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int valores[24-1],asd=0,medd,mediafinal=0;
     int media1[24-1],media2=0,media3=0,mediaa[24-1];
    for(int i=0;i<24;i++)
    cin >> asd >>valores[i];

    media2=(valores[21]+valores[22]+valores[23])/3;

    for(int i=0;i<12;i++)
    {
        mediaa[i]=(valores[i]+valores[i+12])/2;
        //printf("\n%d %d\n",valores[i],valores[i+12]);
    }
    for(int i=0;i<12;i++)
    {
        mediafinal=mediaa[i]+mediafinal;
       // printf("\n%d\n",mediaa[i]);
    }
    mediafinal=mediafinal/12;







    printf("%d\n",(mediafinal+media2)/2-1);



}
