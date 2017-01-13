args = commandArgs(trailingOnly=TRUE)

if(length(args) != 3){
  stop("Usage MonthWiseHighLow StockCode Month Year")
}

args[1] -> stockCode
as.numeric(args[2]) -> month
as.numeric(args[3]) -> year



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



