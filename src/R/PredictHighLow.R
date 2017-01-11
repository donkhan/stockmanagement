args = commandArgs(trailingOnly=TRUE)
"RELINFRA.NS" -> stockCode
10 -> n

Sys.Date() -> today
if(length(args) >= 1){
  args[1] -> stockCode;  
}
if(length(args) >= 2){
  as.numeric(args[2]) -> n
}


format(today,"%d") -> date
format(today,"%m") -> month
format(today,"%Y") -> year

if(length(args) >= 3){
  args[3] -> date
}

if(length(args) >= 4){
  args[4] -> month
}

if(length(args) >= 5){
  args[5] -> year
}
as.numeric(month)-1 -> month

paste("http://real-chart.finance.yahoo.com/table.csv?s=",stockCode,
      "&amp;d=",month,"&amp;e=",date,"&amp;f=",year,
      "&amp;g=d&amp;a=7&amp;b=21&amp;c=2006&amp;ignore=.csv",sep="") -> url
read.csv(url(url)) -> rawData

head(rawData["High"],n) -> h
head(rawData["Low"],n) -> l
head(rawData["Close"],1) -> lx
tail(head(rawData["Close"],n+1),n) -> x

l$Low -> lv
h$High -> hv
x$Close -> cv

lm(lv~cv) -> low.model
low.model$coefficients -> coeff
coeff[1] + lx*coeff[2] -> pl

lm(hv~cv) -> high.model
high.model$coefficients -> coeff
coeff[1] + lx*coeff[2] -> ph

message("---------------------------------------------------")

message(stockCode, "-",date,"/",as.numeric(month)+1,"/",year)
message("Day Close Value " , lx)
message(n ," day values are sampled")
message("Top Sample High Value ", hv[1]," Low Value ",lv[1]," Close Value ",cv[1])

message("Last Sample High Value ", hv[n]," Low Value ",lv[n]," Close Value ",cv[n])
message("Next Day Projected High = " , format(ph,2))
message("Next Day Projected Low = " , format(pl,2))
message("---------------------------------------------------")      
        



