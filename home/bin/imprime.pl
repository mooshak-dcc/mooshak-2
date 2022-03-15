#!/usr/bin/perl

use strict;
use PostScript::TextBlock;

my $user = shift;
my $problema = shift;
my $fich = shift;
my $submission = shift;


system("cp $fich $fich.bak");

open(FICH, "< $fich.bak");
open(FICH1, ">$fich");

while(<FICH>) {

    s|\\|\\\\|g; 
    print FICH1 $_;

}

close(FICH);
close(FICH1);

my $pid=$$;

my $print="/tmp/resultado-$pid.ps";

my $tb = new PostScript::TextBlock;

$tb->addText( text => "Equipa: ".$user . "\n",
	      font => 'CenturySchl-Ital',
	      size => 24,
	      leading => 100
	      );

$tb->addText( text => "Problema: ".$problema . "\n",
	      font => 'URWGothicL-Demi',
	      size => 14,
	      leading => 36
	      );

$tb->addText( text => "Ficheiro: ".$fich . "\n",
	      font => 'URWGothicL-Demi',
	      size => 14,
	      leading => 36
	      );

$tb->addText( text => "Tempo: ".$submission . "\n",
	      font => 'URWGothicL-Demi',
	      size => 14,
	      leading => 36
	      );



open I, $fich;
undef $/;
my $codigo = <I>;

open OUT, ">$print";


my $pages = 1;

my ($code, $remainder) = $tb->Write(572, 752, 20, 772);
print OUT "%!PS-ADOBE-2.0";
print OUT "%%Page:$pages\n";

print OUT $code;

print OUT "showpage\n";

$remainder->addText( text => $codigo,
  		     font => 'URWGothicL-Demi',
  		     size => 12,
  		     leading => 14
  		     );

while ($remainder->numElements) {
    $pages++;
    print OUT "%%Page:$pages\n";
    ($code, $remainder) = $remainder->Write(572, 752, 20, 772);
    print OUT $code;
    print OUT "showpage\n";
}



system("lpr $print");
system("rm $print");
