#include <stdio.h>

int main()
{
	char t[100001];
	int n, d, idx, mai, cont;
	mai = 0;
	cont = 0;
	scanf("%s", t);
	for(idx=0;t[idx]!='\0';idx++)
	{
		if(t[idx] == '1')
			cont++;
		if(t[idx] == '0')
			cont = 0;
		if(mai < cont)
			mai = cont;
	}
	scanf("%d", &n);
	cont = 0;
	for(idx=0; idx<n; idx++)
	{
		scanf("%d", &d);
		if(d < mai)
			cont++;
	}
	printf("%d\n", cont);
	return 0;
}

