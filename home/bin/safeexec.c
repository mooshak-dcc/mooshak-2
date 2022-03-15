/*
 safeexec
 Executar um comando num ambiente protegido
 pbv, 1999-2000

Modificado para reportar estatisticas por mcc, 2003
*/

#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/resource.h>
#include <signal.h>

#include <string.h>
#include <errno.h>


#define NOBODY 65534    /* utilizador abaixo de cão */
#define MBYTE  (1024*1024)

#define TIMEMAXCHARS 20

int child_pid;          /* pid of the child process */

struct rlimit cpu_timeout = {5,5};    /* max cpu time (seconds) */

struct rlimit max_stack = {1*MBYTE, 1*MBYTE};     /* max stack size */
struct rlimit max_data  = {2*MBYTE, 2*MBYTE};     /* max data segment size */
struct rlimit max_core  = {0, 0};                 /* max core file size */
struct rlimit max_rss   = {4*MBYTE, 4*MBYTE};     /* max resident set size */

struct rlimit max_processes = {0, 0}; /* max number of processes */

int userid = NOBODY;		       /* ZP: uid for nobody */
int real_timeout = 30;                 /* max real time (seconds) */

#define BUFFSIZE 256
char rootdir[BUFFSIZE];


static struct timeval structtime_subtract(struct timeval end_time, struct timeval init_time)
{
  long int end_time_us = end_time.tv_sec*1000000 + end_time.tv_usec;
  long int init_time_us = init_time.tv_sec*1000000 + init_time.tv_usec;

  end_time.tv_sec = ( end_time_us - init_time_us ) / 1000000;
  end_time.tv_usec = ( end_time_us - init_time_us ) % 1000000;
  return end_time;
}


static char *tv2string(struct timeval tv)
{
  char *formatted = (char *)malloc(TIMEMAXCHARS);
  sprintf(formatted,"%d.%02d",tv.tv_sec, tv.tv_usec/10000);
  return(formatted);
}

int output_resources=0;			/* output resources in stderr */
struct timeval init_time = {0,0};

/* show child process resource usage (as would time -f %e %S %U %M %k )*/
void show_resources(int status) {
  struct rusage resources;
  struct timeval end_time = {0,0};

  if(output_resources) {
    /* End chronometer for running child */
    gettimeofday(&end_time,NULL);
    //printf("End time = %s\n", tv2string(end_time));
    end_time = structtime_subtract(end_time,init_time);
    
    /* getrusage segcores if struct rusage is not clean */
    memset((void *)&resources, 0, sizeof(struct rusage));
    if(getrusage(RUSAGE_CHILDREN, &resources))
      {
	perror("Could not get child used resources");
	exit(status);
      }
    
    fprintf(stderr," %s %s %s %ld %ld\n", 
	    tv2string(end_time), 
	    tv2string(resources.ru_stime), 
	    tv2string(resources.ru_utime), 
	    resources.ru_maxrss,
	  resources.ru_nsignals);
  }
}



/* alarm handler */
void handle_alarm(int sig) {
  int status=1; /* ??? */
  fprintf(stderr, "timed-out after %d seconds\n", real_timeout);
  show_resources(status);
  fflush(stderr);
  /*
  fprintf(stdout, "timed-out after %d seconds\n", real_timeout);
  fflush(stdout);
  */
  kill(child_pid,9);   /* kill child */
  exit(2);
}
 
void usage(int argc, char **argv) {

  fprintf(stderr, "usage: %s [ options ] cmd [ arg1 arg2 ... ]\n", argv[0]);
  fprintf(stderr, "available options are:\n");
  fprintf(stderr, "\t-c <max core file size> (default: %d)\n", 
	  max_core.rlim_max);
  fprintf(stderr, "\t-d <max process DATA segment> (default: %d bytes)\n",
	  max_data.rlim_max);
  fprintf(stderr, "\t-s <max process STACK segment> (default: %d bytes)\n",
	  max_stack.rlim_max);
  fprintf(stderr, "\t-m <max process RSS> (default: %d bytes)\n",
	  max_rss.rlim_max);
  fprintf(stderr, "\t-u <max number of users's child procs> (default: %d)\n",
	  max_processes.rlim_max);
  fprintf(stderr, "\t-t <max cpu time> (default: %d secs)\n",
	  cpu_timeout.rlim_max);
  fprintf(stderr, "\t-T <max real time> (default: %d secs)\n",
	  real_timeout);
  fprintf(stderr, "\t-i <nobody userid or userid >= 500> (default: %d)\n",userid);

  fprintf(stderr, "\t-R <root directory> (default: cwd) [DEPRECATED]\n");
  fprintf(stderr, "\t-r <output resources in stderr>\n");

}

int main(int argc, char **argv) { 
  int status, opt;

  /* parse command-line options */
  getcwd(rootdir, BUFFSIZE);  /* default: use cwd as rootdir */
  while( (opt=getopt(argc,argv,"c:d:m:s:t:T:u:i:R:r")) != -1 ) {
    switch(opt) {
    case 'c': max_core.rlim_max = max_core.rlim_cur = atoi(optarg);
      break;
    case 'd': max_data.rlim_max = max_data.rlim_cur = atoi(optarg);
      break;
    case 'm': max_rss.rlim_max = max_rss.rlim_cur = atoi(optarg);
      break;
    case 's': max_stack.rlim_max = max_stack.rlim_cur = atoi(optarg);
      break;
    case 't': cpu_timeout.rlim_max = cpu_timeout.rlim_cur = atoi(optarg);
      break;
    case 'T': real_timeout = atoi(optarg);
      break;
    case 'u': max_processes.rlim_max = max_processes.rlim_cur = atoi(optarg);
      break;
    case 'i': userid = atoi(optarg); /* ZP: set uid for nobody */
      break;
    case 'r': output_resources=1; /* ZP: output resources in stderr */
      break;
    case 'R': strcpy(rootdir, optarg);  /* root directory */
      break;
      
    case '?': usage(argc,argv);
      exit(-1);
    }
  }

  if(optind >= argc || userid < 500 ) {  /* no more arguments */
    usage(argc,argv);
    exit(-1);
  }

  /* ZP: switching off chroot (its no use changing it to /)*/
  /* change the root directory (ZP: and working dir, if not root)
  if((strcmp(rootdir,"/")==0 || chdir(rootdir))  && chroot(rootdir)) { 
    fprintf(stderr,"%s\n",strerror(errno));
    fprintf(stderr,"%s: unable to change root directory to %s\n",
	    argv[0], rootdir); 
    exit(-1); 
  } 
  */
  
  /* change the user id to 'nobody' (ZP: or a given uid)*/
  if(setuid(userid)<0 || getuid()==0) {
    fprintf(stderr,"%s\n",strerror(errno));
    fprintf(stderr, "%s: unable to change uid to %d\n", argv[0],userid);
    exit(-1);
  }
  
  if((child_pid=fork())) { 

    /* Start chronometer for running child */
    gettimeofday(&init_time,NULL);
    //printf("Star time = %s\n", tv2string(init_time));

    /* ------------------- parent process ----------------------------- */
    /* set this limit also for parent */
    setrlimit(RLIMIT_CORE, &max_core);

    if(real_timeout > 0) {
      alarm(real_timeout);   /* set alarm and wait for child execution */
      signal(SIGALRM, handle_alarm);
    }
    wait(&status);

    // check if child got an uncaught signal error & reproduce it in parent
    if(WIFSIGNALED(status)) {
      //raise(WTERMSIG(status));      
      fprintf(stderr,"Command terminated by signal %d\n",status);      
    } else {
      //otherwise just report the exit code:
      if(status)
	fprintf(stderr,"Command exited with non-zero status %d\n",status);
    }
    // NOTE: report message reproduce those of time(1)
    show_resources(status);
    exit(0);
  } else {
    /* ------------------- child process ------------------------------ */
    /* attempt to change the hard limits */
    if( setrlimit(RLIMIT_CPU, &cpu_timeout) || 
	setrlimit(RLIMIT_DATA, &max_data) ||
	setrlimit(RLIMIT_STACK, &max_stack) ||
	setrlimit(RLIMIT_CORE, &max_core) ||
	setrlimit(RLIMIT_RSS, &max_rss) ||
	setrlimit(RLIMIT_NPROC, &max_processes) ) {
      fprintf(stderr,"%s\n",strerror(errno));
      fprintf(stderr, "%s: can't set hard limits\n", argv[0]);
      exit(-1);
    }

    /* attempt to exec the child process */
    if(execv(argv[optind],&argv[optind]) < 0) {
      fprintf(stderr,"%s\n",strerror(errno));
      fprintf(stderr, "%s: unable to exec %s\n", argv[0], argv[optind]);
      exit(-1);
    } 
  }

}


