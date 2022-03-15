#include <stdio.h>
#include <stdlib.h>

int main()
{
    int i, j, N, seq[1000];
    int r = 0;

    scanf("%d", &N);
    for(i=0; i<N; i++) {
        scanf("%d", &seq[i]);
    }

    for(i=1; i<N-1; i++) {
        if(seq[i] > seq[i-1] && seq[i] > seq[i+1] && (seq[i-1]*2 < seq[i] && seq[i+1]*2 < seq[i])) r++;
    }

    printf("%d\n", r);

    return 0;
}
