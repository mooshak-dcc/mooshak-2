#include <stdio.h>

int main(void){
	int i, x, ano1, ano2, mes1, mes2, mes3, ind ;
	scanf("%d%d", &x, &ano1);
	for (i=0; i < 11; i++)
		scanf("%d%d", &x,&x);
	scanf("%d%d", &x, &ano2);
	for (i=0; i < 8; i++)
		scanf("%d%d", &x,&x);
	scanf("%d%d", &x, &mes1);
	scanf("%d%d", &x, &mes2);
	scanf("%d%d", &x, &mes3);

	ind = ((ano1+ano2)/2 + (mes1+mes2+mes3)/3) / 2; 
	printf("%d\n", ind);
	return 0;
}

