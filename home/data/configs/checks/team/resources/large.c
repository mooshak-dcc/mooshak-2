# include <stdio.h>
# include <stdlib.h>

# define MAX	100000000

int main() {
  long a[MAX];
  int s = 0;
  int i;
  
  for(i=0;i<MAX;i++) {
    a[i]=0;
    s += a[i];
  }
  exit(0);
}
