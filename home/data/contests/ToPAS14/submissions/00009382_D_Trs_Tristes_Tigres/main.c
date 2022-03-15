#include <stdio.h>
#include <stdlib.h>

int main()
{
    int i, vendas, datas[24][3], media1, media2, contaMeses, ano, mes;
    char dataInput[7], tmpAno[5], tmpMes[3];

    for(i=0; i<24; i++){
        scanf("%s %d", dataInput, &vendas);

        tmpAno[0] = dataInput[0];
        tmpAno[1] = dataInput[1];
        tmpAno[2] = dataInput[2];
        tmpAno[3] = dataInput[3];
        tmpAno[4] = '\0';

        tmpMes[0] = dataInput[4];
        tmpMes[1] = dataInput[5];
        tmpMes[2] = '\0';

        datas[i][0] = atoi(tmpAno);
        datas[i][1] = atoi(tmpMes);
        datas[i][2] = vendas;
    }

    media1 = (datas[21][2] + datas[22][2] + datas[23][2]) / 3;

    ano = datas[23][0];
    mes = datas[23][1];
    if(datas[23][1] == 12) {
        ano++;
        mes = 1;
    } else {
        mes++;
    }

    media2 = 0; contaMeses = 0;
    for(i=23; i>=0; i--) {
        if(datas[i][1] == mes) {
            media2 += datas[i][2];
            contaMeses++;

            if(contaMeses == 2) break;
        }
    }


    media2 = media2 / contaMeses;

    printf("%d\n", (media1+media2)/2);

    return 0;
}
