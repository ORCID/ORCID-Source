
#Convert any works ids between 4 and 7 digits to be delete statements
sed "s/\([0-9]\{4,7\}\)/delete from work where work_id=\0;/g" $1 > $2
