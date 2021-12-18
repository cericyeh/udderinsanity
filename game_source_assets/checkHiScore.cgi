#! /usr/bin/perl

# ----------------------------------------------------------------------
# parse the request

$method = $ENV{'REQUEST_METHOD'};
$length = $ENV{'CONTENT_LENGTH'};

if ($method eq "GET") {
    # data posted via get
    $dataBuffer = $ENV{'QUERY_STRING'};
} else {
    read(STDIN,$dataBuffer,$length); 
# via POST
}

# now split each & delmited item into separate tokens and translate
# the tokens

@tokenList = split(/&/,$dataBuffer);
foreach $token (@tokenList) {
    #separate out key name and value fields
    ($name, $value) = split(/=+/,$token);

    # + --> " "
    $value =~ tr/\+/ /;
    $name =~ tr/\+/ /;

    #convert %XX back to a control sequence
    $value =~ s/%(..)/pack("c",hex($1))/ge;
    $name =~ s/%(..)/pack("c",hex($1))/ge;

    #kill ssi
    $value =~ s/<!//ge;
    $name =~ s/<!//ge;

    # create the entry in the hash list
    $dataList{$name} = $value;
}

# ----------------------------------------------------------------------
$highScoreFile = "highudder.txt";   

$score = $dataList{'score_val'};

# open the datafile.  If there is an error, return error

print "Content-type: text/plain\n\n";  # standard header
#print "Content-type: text/html\n\n";  # standard header

#print "<html><body>\n";

if (!open(HISCOREFILE, $highScoreFile)) {
# return error statement and exit
#    print "<p>error hiscores unavailable</p>";
    print "error hiscores unavailable";
} else {
# return the highscore list and return if user is on highscore list
    @hiScoresList = <HISCOREFILE>;
    
    $newHighScore = 0; # set to 1 to indicate highscore
    # now determine if the score is in the highscore or not
    foreach $item (@hiScoresList) {
	($name,$scoreVal,$email) = split(/\+/,$item);
#	print "<p>$score $name $scoreVal</p>\n";
	if ((int $score) > (int $scoreVal)) {
#	    $intVal1 = (int $score);
#	    $intVal2 = (int $scoreVal);
#	    print "<p>Found hi scorer $intVal1 $intVal2</p>";
	    $newHighScore = 1; # come on, find a loop breaking mechanism!
	}
    }
    if ($newHighScore == 1) {
#	print "<p>new high score</p>";
	print "new high score";
    } else {
	print "no new score!";
    }
}

close HISCOREFILE;

#print "</html></body>\n";
