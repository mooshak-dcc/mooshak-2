#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int DJ=0,D=0,E=0,A=0,B=0;
    float C=0;
    float ano=0,mes=0,dia=0;
    cin >> ano >> mes >> dia;
    int a,m,d;
    a=ano;
    m=mes;
    d=dia;
    if(mes<3)
    {
        ano=ano-1;
        mes=mes+12;
    }
    A=ano-1;
    B=A/4;

    if(dia > 15 && mes > 10 && ano > 1582)
    C=2-A+B;
    else
    C=0;

    D=(365.25*(ano+4716));
    E=(30.6001*(mes+1));
    DJ=D+E+dia+C-1524;

    if(a > 1582)
    cout << DJ-13<< endl;
    else
    cout << DJ << endl;
    return 0;
}
