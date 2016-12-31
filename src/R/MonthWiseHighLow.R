args = commandArgs(trailingOnly=TRUE)
"RELINFRA.NS" -> stockCode
month=10
year=2016

if(length(args) >= 1){
  args[1] -> stockCode
}

if(length(args) >= 2){
  as.numeric(args[2]) -> month
}
if(length(args) >= 3){
  as.numeric(args[3]) -> year
}


paste("http://real-chart.finance.yahoo.com/table.csv?s=",stockCode,"&a=",month,"&b=1&c=",year,"&d=",month,"&e=30&f=",year,
"&g=d&ignore=.csv",sep="") -> url


read.csv(url(url)) -> rawData
max(rawData$High) -> maximum
min(rawData$Low) -> minimum

message("Stock Code ",stockCode)
message("Time Frame ",month,"-",year)
message("URL ",url)
message("Max = ",maximum)
message("Min = ",minimum)



