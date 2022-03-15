#include <stdio.h>
#include <stdlib.h>

int main()
{
    int data[5], vendas,i,j,k, v[23];
    int m[5][23];


    for(i=0;i<=24;i++){
        for(j=0;j<=5;j++){
            scanf("%d",&data[j]);
        }
        scanf("%d",&vendas);

        for(i=0;i<=24;i++){
            for(j=0;j<=5;j++){
                m[i][j]=data[j];
            }
        }
        for(k=0; k<=23;k++){
            v[i]=vendas;
        }
    }

    mes=data[5]+1;


    }


    return 0;
}
