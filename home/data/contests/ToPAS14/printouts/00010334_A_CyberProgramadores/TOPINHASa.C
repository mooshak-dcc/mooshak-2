#include<stdio.h>
int main(){
     int ano,mes,dia,A,B,C,D,E,DJ;
        scanf("%d",&ano);
        scanf("%d",&mes);
        scanf("%d",&dia);
        if(mes<3){
           ano=ano-1;
           mes=mes+12;
        A=ano/100;
        B=A/4;
        }
        if((ano==1582) && (mes==10) && (dia==15)){
            C=2-A+B;
         }
        if((ano==1582) && (mes==10) && (dia==4)){
            C=0;
     }
    D=(365,25*(ano+4716));
    E=(30,6001*(mes+1));
    DJ=D+E+dia+C-1524;

    printf("%d\n",DJ);
    return 0;
    }
