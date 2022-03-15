#include <stdio.h>
#include <stdlib.h>

int main()
{
   int ano, mes, dia,d,e,dj;
   double a, b, c;

   scanf("%d", &ano);
   scanf("%d", &mes);
   scanf("%d", &dia);

   if(mes<3){
    ano=ano-1;
    mes=mes+12;

   }
    a=ano/100;
    b=a/4;

   if(dia>15 && mes>10 && ano>1582){
   c=2-a+b;
   }

   if(dia<=4 && mes<=10 && ano<=1582){
    c=0;
   }

    d=365.25*(ano+4716);
    e=30.6001*(mes+1);

   dj=d+e+dia+c-1524;

   printf("%d\n", dj);


    return 0;
}
