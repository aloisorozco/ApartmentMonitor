local userKey = KEYS[1]

local timeOfRequest = tonumber(ARGV[1])
local CACHE_TTL = tonumber(ARGV[2])
local API_LIMIT = tonumber(ARGV[3])

local isRateLimited = 0

local userExistsAndUpdatedTTL = redis.call("pexpire", userKey, CACHE_TTL)

if userExistsAndUpdatedTTL then
    local cutoff = timeOfRequest - CACHE_TTL
    redis.call("zremrangebyscore", userKey, 0, cutoff)
    local numOfRequests = redis.call("zcard", userKey)

    if numOfRequests >= API_LIMIT then
        isRateLimited = 1
    else
        redis.call("zadd", userKey, timeOfRequest, timeOfRequest)
        isRateLimited = 0
    end
else
    redis.call("zadd", userKey, timeOfRequest, timeOfRequest)
    redis.call("pexpire", userKey, CACHE_TTL)
    isRateLimited = 0
end

return isRateLimited