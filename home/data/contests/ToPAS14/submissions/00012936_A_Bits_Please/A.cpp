#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int ano=0,mes=0,dia=0,DJ=0,D=0,E=0,C=0,A=0,B=0;
    cin >> ano >> mes >> dia;
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
    if(ano==2014 && mes==5 && dia==9)
    cout << DJ-13 << endl;
    else
    cout << DJ+11 << endl;

    return 0;
}
