#include <stdio.h>
#include <stdlib.h>


int main()
{
    int values[24],sum1 = 0,sum2 = 0,media1 = 0,media2 = 0,media_total=0,i;
    char dates[24][7],last_month[2];
    for (i=0;i<24;i+=1)
    {
        scanf("%s %d",dates[i],&values[i]);
    }
    for (i=23;i>-1;i-=1)
    {
        if (i>=21)
        {
            sum1 +=values[i];
        }
        if (i==23)
        {
            if (dates[i][5]=='9')
            {
                last_month[0]='1';
                last_month[1] = '0';
            }
            else if (dates[i][4] == '1' && dates[i][5]=='2')
            {
                last_month[0]='0';
                last_month[1] = '1';
            }
            else
            {
                last_month[0]=dates[i][4];
                last_month[1] = dates[i][5]+1;
            }
        }
        if (dates[i][4]==last_month[0] && dates[i][5] == last_month[1] && i<21)
        {
            sum2 +=values[i];
        }
    }
    media1 = sum1/3;
    media2 = sum2/2;
    media_total = (media1+media2)/2;
    printf("%d\n",media_total);
    return 0;
}
