#include <iostream>

using namespace std;

int main(){
    int dia,mes,ano,A,B,C,D,E,DJ;
    cin>>ano>>mes>>dia;
    if(mes<3){
        ano = ano - 1;
        mes = mes + 12;
        }
    A = ano/100;
    B = A/4;

    if(dia>15 and mes>10 and ano>1582){
        C = 2 - A + B;
    }


    if(dia<=4 and mes<=10 and ano<=1582){
        C = 0;
    }

    if(dia>4 && dia<15 and mes>10 and ano>1582){
        dia = 15;



}
    D = 365.25 * (ano + 4716);
    E = 30.6001 * (mes + 1);

    DJ = D + E + dia + C - 1524;

    cout<<DJ-13<<endl;


}
