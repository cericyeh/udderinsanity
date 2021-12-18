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

# 'constants'
$MAX_HIGH_SCORES = 10;

# parameters
$name = $dataList{'name'};
$email = $dataList{'email'};
$score = $dataList{'score_val'};

# remove '+' characters from name and email
$name =~ s/\+/ /ge;
$email =~ s/\+/ /ge;

$authentication = $dataList{'udderVal'};  

if ($authentication eq "swinging udders") {
# open the datafile.  If there is an error, return error

print "Content-type: text/plain\n\n";  # standard header
#print "Content-type: text/html\n\n";  # standard header

#print "<html><body>\n";

if (!open(HISCOREFILE, $highScoreFile)) {
# return error statement and exit
    print "error hiscores unavailable";
} else {
    @hiScoresList = <HISCOREFILE>;

#    print "<p>@hiScoresList</p><hr>\n";

    # place new placer into the new high score list
    $currLine = 0;  

    $scoreSet = 0; # flag to determine if new score has been set or not
    # now determine if the score is in the highscore or not
    foreach $item (@hiScoresList) {
	# extract '+' and newlines from entry
	$item =~ tr/\\n//;
	# while still in top 10, insert entries into the newList
	if ($currLine < $MAX_HIGH_SCORES) {
	    ($oldName,$oldScore,$oldEmail) = split(/\+/,$item);


#	    print "<p>name=$oldName score=$oldScore email=$oldEmail</p>\n";
	    if (((int $score) > (int $oldScore)) &&
		($scoreSet == 0)) {
		# insert new placer and place old player after, inc currLine
		$newList[$currLine] = "$name+$score+$email\n";
		$currLine++;
		$newList[$currLine] = "$oldName+$oldScore+$oldEmail";
		$scoreSet = 1; # trigger flag
	    } else {
		# insert list entry
		$newList[$currLine] = "$oldName+$oldScore+$oldEmail";
	    }
#	    print "<p>line = $currLine   $newList[$currLine]</p>\n";
	    $currLine++; #increment line
	}
    }
}
close HISCOREFILE;

#print "<hr>\n";

if (!open(HISCOREFILE,"> $highScoreFile")) {
print "error highscores unavailable";  # cannot write to file
} else {
#    print @newList;
    print HISCOREFILE @newList;
    close HISCOREFILE;
}
} # end of actual submission
print "submission passed successfully";
#print "</body></html>\n";
