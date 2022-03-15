#include <stdio.h>
#include <math.h>

main()
{
    int anomes[24];
    int vendas[24];
    int i;
    int mediatotal;
    float media3m;
    float media2m;

    for(i=1;i<=24;i++){
        scanf("%d %d",&anomes[i],&vendas[i]);
    }
    media3m=(vendas[24]+vendas[23]+vendas[22])/3;
    media2m=(vendas[24]+vendas[24-12])/2;
    mediatotal=trunc((media3m+media2m)/2);
    printf("%d\n",mediatotal);
    return 0;
}
