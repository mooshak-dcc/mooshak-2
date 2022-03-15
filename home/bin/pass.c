#define _XOPEN_SOURCE
#include <unistd.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>

#define OK	0
#define ERROR	1

/* From local_passwd.c (C) Regents of Univ. of California blah blah */
static unsigned char itoa64[] =         /* 0 ... 63 => ascii - 64 */
        "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


void to64(register char *s, register long v, register int n) {
  while (--n >= 0) {
      *s++ = itoa64[v&0x3f];
        v >>= 6;
    }
}

int  status;
char buffer[256];

int go(int argc, char **argv) { 
  char salt[3];

  switch(argc) {
  case 2: 
    /* plain -> crypted */
    (void)srand((int)time((time_t *)NULL));
    to64(&salt[0],rand(),2);    
    sprintf(buffer,"%s\n",crypt(argv[1],salt));    
    return OK;
  case 3:
    /* crypted plain -> [ "correct" | "incorrect" ]*/
    if (!strcmp(argv[1], crypt(argv[2], argv[1])))
      sprintf(buffer,"correct\n");
    else
      sprintf(buffer,"incorrect\n");
   return OK;
  default:
    sprintf(buffer,"usage:\n\t%s plain\t\t->\tcrypted\n",argv[0]);
    sprintf(buffer,"\t%s crypted plain\t->",argv[0]);
    sprintf(buffer,"\t[ \"correct\" | \"incorrect\" ]\n");
    return ERROR;	
  }
    
}

int main(int argc, char **argv) { 
 
  int status=go(argc,argv);
  if(status)
    fprintf(stderr,"%s",buffer);
  else 
    printf("%s",buffer);
  exit(status);
}

/****
   Bind pass() to tcl interpreter as command 'bind'
   Requires changing makefile to produce loadable library (pass.so)

   It has been commented out to avoid installation problems
   in distributions that where tcllib.so is in a separate package
   (such as suse)

#include <tcl.h>
#define VERSION "1.0"


int pass(clientData, interp, argc, argv)
     ClientData   clientData;
     Tcl_Interp  *interp;
     int          argc;
     char       **argv;
{
  int status=go(argc,argv);
  Tcl_SetResult(interp,buffer, TCL_VOLATILE);

  if(status==OK)
  return TCL_OK;
  else
  return TCL_ERROR;
}


int pass_Init(interp)
     Tcl_Interp *interp;
{

  Tcl_PkgProvide (interp, "pass",VERSION);

  Tcl_CreateCommand (interp, "pass", pass, (ClientData)NULL, NULL);

  return TCL_OK;
}

****/
