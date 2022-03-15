#include <stdio.h>
int main()
{
    int ano,mes,dia;
    int a,b,c,d,e;
    int dj;
    scanf ("%d %d %d",&ano,&mes,&dia);

    if(mes<3)
    {
        ano=ano-1;
        mes=mes+12;
    }

    a=ano/100;
    b=a/4;
    if(dia<15 && mes<10 && ano<1582)
        c=0;
    else
        c=2-a+b;

    d=(365.25*(ano+4716));
    e=(30.6001*(mes+1));

    dj=d+e+dia+c-1524;
    printf ("%d\n",dj);

    return 0;
}
