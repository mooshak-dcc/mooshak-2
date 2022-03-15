#include<stdio.h>
int main(){
     int ano,mes,dia,a,b,c,d,e,dj;
        scanf("%d %d %d",&ano,&mes,&dia);
        if(mes<3){
           ano=ano-1;
           mes=mes+12;
        a=ano/100;
        b=a/4;
        }
        if((ano=1582) && (mes=10) && (dia=15)){
            c=2-a+b;
         }
        if((ano=1582) && (mes=10) && (dia=4)){
            c=0;
        }
    d=365,25*(ano+4716);
    e=30,6001*(mes+1);
    dj=d+e+dia+c-1524;
    printf("%d\n",dj);
    }


