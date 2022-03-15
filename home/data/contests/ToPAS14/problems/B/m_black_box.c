#include <stdio.h>

int main(void)
{
  int x, y, z;
  int i;
  int n;
  int r = 0;
  scanf("%d", &n);
  scanf("%d%d", &x, &y);
  for (i = 2; i < n; i++)
  {
    scanf("%d", &z);
    if (2 * x < y && y > 2 * z)
      r++;
    x = y;
    y = z;
  }
  printf("%d\n", r);
  return 0;
}
