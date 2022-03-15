#include <stdio.h>
#include <stdlib.h>

int checkData1(int ano, int mes, int dia) {

    if(ano > 1582) return 1;
    else if(ano == 1582) {
        if(mes > 10) return 1;
        else if(mes == 10) {
            if(dia > 15) return 1;
        }
    }

    return 0;
}

int main()
{
    int AAAA, MM, DD;
    int A, B, C, D, E, DJ, ano, mes, dia;
    float tmp, conta;

    scanf("%d %d %d", &AAAA, &MM, &DD);
    ano = AAAA; mes = MM; dia = DD;

    if(MM < 3) {
        AAAA--;
        MM+=12;
    }

    A = AAAA/100;
    B = A/4;

    if(checkData1(ano, mes, dia)) {
        C = 2 - A + B;
    } else {
        C = 0;
    }

    tmp = 365.25;
    conta = tmp * ((float)AAAA + (float)4716);
    D = (int)conta;
    tmp = 30.6001;
    conta = tmp * ((float)MM + (float)1);
    E = (int)conta;

    DJ = D + E + DD + C - 1524;

    printf("%d\n", DJ);

    return 0;
}
