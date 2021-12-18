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

if (!open(HISCOREFILE, $highScoreFile)) {
# return error statement and exit
    print "error hiscores unavailable";
} else {
# return the highscore list and return if user is on highscore list
    @hiScoresList = <HISCOREFILE>;
    print @hiScoresList;
}

close HISCOREFILE;


