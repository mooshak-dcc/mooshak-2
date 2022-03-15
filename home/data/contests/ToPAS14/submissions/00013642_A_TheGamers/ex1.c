#include <stdio.h>
int main()
{
    int ano,mes,dia;
    int a,b,c,d,e;
    int dantes;
    int dj;
    scanf ("%d %d %d",&ano,&mes,&dia);

    if(mes<3)
    {
        ano=ano-1;
        mes=mes+12;
    }

    a=ano/100;
    b=a/4;
    c=2-a+b;

    dantes=4716+ano;
    d=(365.25*dantes);
    e=(30.6001*(mes+1));

    dj=d+e+dia+c-1524;
    printf ("%d\n",dj);
    return 0;
}
