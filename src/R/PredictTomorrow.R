args = commandArgs(trailingOnly=TRUE)
10 -> n

Sys.Date() -> today
if(length(args) != 5){
  stop("Usage Rscript <Script> StockCode Frequency Low High Close")
}

args[1] -> stockCode; 
as.numeric(args[2]) -> n
args[3] -> tl
args[4] -> th
args[5] -> tc

format(today,"%d") -> date
format(today,"%m") -> month
format(today,"%Y") -> year

as.numeric(month)-1 -> month

paste("http://real-chart.finance.yahoo.com/table.csv?s=",stockCode,
      "&amp;d=",month,"&amp;e=",date,"&amp;f=",year,
      "&amp;g=d&amp;a=7&amp;b=21&amp;c=2006&amp;ignore=.csv",sep="") -> url
read.csv(url(url)) -> rawData
head(rawData,n) -> rawData

data.frame(Low=0,High=0,Close=0) ->newData

newData[1,1] = tl
newData[1,2] = th
newData[1,3] = tc

#for(x in seq(1:n)){
#  newData[x+1,] = rawData[x,c(4,3,5)]
#}

rbind(newData,rawData[1:n-1,c(4,3,5)]) -> newData

transform(newData, Low = as.numeric(Low), Close = as.numeric(Close), 
          High = as.numeric(High)) -> newData


head(newData["High"],n) -> h
head(newData["Low"],n) -> l
tail(head(newData["Close"],n+1),n) -> x
head(newData["Close"],1) -> lx

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




