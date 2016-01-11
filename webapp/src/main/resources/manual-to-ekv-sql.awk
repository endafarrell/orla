{
    tmp="echo " $0 " | openssl md5 | cut -f2 -d\" \""
tmp | getline cksum
close(tmp)
print cksum "|BolusEvent|{\"type\":\"Std\",\"cmd\":\"null\",\"id\":\"null\",\"startTime\":\"2015-07-0" $1 " " $2 ":00.000+0200\",\"source\":\"Manual\",\"text\":\"bolus\",\"value\":" $3 ",\"unit\":\"IU\",\"day\":\"XXX\",\"hhmm\":\"" $2 "\",\"time_pct\":00,\"clazz\":\"BolusEvent\"}|2015-07-09 18:00:00"
}