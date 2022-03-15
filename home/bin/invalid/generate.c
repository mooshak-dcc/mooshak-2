#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <signal.h>
#include <stdlib.h>

#define SIZE 4096

void function (const char *p)
{
  printf ("%s () { kill (getpid (), SIGHUP); }\n", p);
}

int main (void)
{
  char p [SIZE];
  printf ("#define SIGHUP %d\n", SIGHUP);
  while (gets (p) != NULL)
    {
      if (strcmp (p, "") == 0)
        continue;
      function (p);
    }
  return (0);
}
