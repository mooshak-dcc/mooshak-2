#include <stdio.h>
#include <stdlib.h>

int main()
{
    int dia, mes, ano;
    int a, b, c, d, e, dj;

    scanf("%d %d %d", &ano, &mes, &dia);

    if (mes < 3){
        ano = ano - 1;
        mes = mes + 12;
    }

    a = ano / 100;
    b = a / 4;
    if (ano > 1582 || (ano == 1582 && mes > 10) || (ano == 1582 && mes == 10 && dia > 15)){
        c = 2 - a + b;
    } else c = 0;

    d = 365.25 * (ano + 4716);
    e = 30.6001 * (mes + 1);

    printf("%d\n", d + e + dia + c - 1524);
    return 0;
}
