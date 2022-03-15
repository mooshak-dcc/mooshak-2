# include <stdio.h>
# include <stdlib.h>

int main() {
  int i=0;

  *(int *)((&i) - (&i)) =0;

  exit(0);
}
