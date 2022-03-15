#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int valores[24-1],asd=0,mediaa,medd;
     int media1[24-1],media2=0,media3=0;
    for(int i=0;i<24;i++)
    cin >> asd >>valores[i];
    media2=(valores[21]+valores[22]+valores[23])/3;

        media1[0]=(valores[9]+valores[21])/2;
        media1[1]=(valores[10]+valores[22])/2;
        media1[2]=(valores[11]+valores[23])/2;
        media1[3]=(media1[0]+media1[1]+media1[2])/3;


    printf("%d\n",media1[3]);



}
