#include <stdio.h>

int main()
{
    int contacasos=0,casos;
    scanf ("%d",& casos);
    int freq[casos],i;

    for(i=0;i<casos;i++)
    {
        scanf ("%d",&freq[i]);
    }

    for(i=0;i<casos;i++)
    {
        if(i==0)
        {
            if(freq[i]>(freq[i+1]*2))
            {
                contacasos++;
            }
        }
        else
        {
            if(i==casos-1)
            {
                if(freq[i]>(freq[i-1]*2))
                {
                    contacasos++;
                }
            }
            else
            {
                if(freq[i]>(freq[i+1]*2) && freq[i]>(freq[i-1]*2))
                {
                    contacasos++;
                }
            }
        }
    }
    printf ("%d\n",contacasos);
    return 0;
}
