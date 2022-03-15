#include <iostream>

using namespace std;

int main()
{

    int ano, mes, dia, A, B, C, D, E, DJ;

    cin>>ano>>mes>>dia;

    if(mes<3){
    ano=ano-1;
    mes= mes+12;
}


    A=ano/100;
    B=A/4;

    if(ano>1582){

    if(mes> 10){

    if(dia>15){
    C= 2 - A+B;
    }}}

    if (ano<=1582){
    if (mes<=10){
    if( dia<=4){
    C=0;}}}



    D=(365.25*(ano+4716));
    E=(30.6001*(mes+1));

    DJ= D+E+dia+C-1524-11;

    cout<<DJ<<endl;

    return 0;
}
