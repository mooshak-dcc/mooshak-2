#include <iostream>
using namespace std;

int main()
{
float A,B,C, D;
int mes,ano,dia,E,DJ;

cin>>ano>>mes>>dia;
if(mes<3){
ano=ano-1;
mes=mes+12;
}
    A=ano/100;

    B=A/4;
if (ano==1582 && mes==10 && dia==4){
C=0;
}else{
    C=2-A+B;
}
    D=365.25*(ano+4716);

    E=30.6001*(mes+1);

    DJ=D+E+dia+C-1524;

    cout << DJ << endl;
    return 0;
}
