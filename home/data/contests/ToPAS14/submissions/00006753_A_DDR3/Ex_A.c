#include <stdio.h>
#include <stdlib.h>

int main()
{
    int ano,mes,dia,temp_a,temp_b,temp_c,temp_d,temp_e, sum = 0;
    scanf("%d %d %d",&ano,&mes,&dia);
    if (mes<3)
    {
        ano -= 1;
        mes += 12;
    }
    temp_a = ano/100;
    temp_b = temp_a/4;
    if (ano<1582)
    {
        temp_c = 0;
    }
    else if (mes<10 && ano==1582)
    {
        temp_c = 0;
    }
    else if (dia<15 && mes == 10 && ano==1582)
    {
        temp_c = 0;
    }
    else
    {
        temp_c = 2-temp_a+temp_b;
    }
    temp_d = 365.25*(ano+4716);
    temp_e = 30.6001*(mes+1);
    sum = temp_d+temp_e+dia+temp_c-1524;
    printf("%d\n",sum);
    return 0;
}
