numbers <- read.csv("/Users/mauricioaniche/Desktop/numbers.csv", header=F)
numbers$V1 <- numbers$V1 / 1024 / 1024
hist(numbers$V1)

summary(numbers$V1)
plot(numbers$V1, type="l")
