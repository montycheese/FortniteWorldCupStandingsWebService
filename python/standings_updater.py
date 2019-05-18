import requests
import json

URL_TEMPLATE = "https://www.epicgames.com/fortnite/competitive/api/leaderboard/epicgames_OnlineOpen_Week{}_{}/OnlineOpen_Week{}_{}_Event2"
SENGAGE_API_URL = "https://5cyv1uh1f2.execute-api.us-east-1.amazonaws.com/Prod"

REGION_CODE_TO_REGION = {
	"NAE": "NAEAST",
	"NAW": "NAWEST",
	"EU": "EUROPE",
	"OCE": "OCEANIA",
	"ASIA": "ASIA",
	"BR": "BRAZIL"
}

def handler(event, context):
  print(json.dumps(event))
  if event["pause"] == True or event["pause"] == "True":
    return
  week = int(event["week"])
  solos = event["solos"] == "True" or event["solos"] == True

  for k, v in REGION_CODE_TO_REGION.items():
    standings = []
    url = URL_TEMPLATE.format(week, k, week, k)
    print("Fetching Standings from EPIC for Week {}, Region {}".format(week, v))
    entries = get_epic_entries(url)
    for entry in entries:
      standing = dict()
      standing["name1"] = entry["displayNames"][0]
      if solos == False:
        standing["name2"] = entry["displayNames"][1]
      standing["region"] = v
      standing["week"] = week
      standing["rank"] = int(entry["rank"])
      standing["solos"] = solos
      try:
        standing["prize"] = int(entry["payout"]["quantity"])
        standing["currencySymbol"] = entry["payout"]["value"]
      except KeyError:
        print("No payout for entry: " + str(entry["displayNames"]))
      standing["points"] = int(entry["pointsEarned"])
      standings.append(standing)
    print("Sending {} standings to server".format(len(standings)))
    requests.put("{}/standings".format(SENGAGE_API_URL), data=json.dumps({"standings" : standings}))

def get_epic_entries(url):
  request = requests.get(url)
  content = json.loads(request.text)
  try:
    entries = content["entries"]
  except KeyError:
    print("No entries available from API, shutting down")
    exit()
  return entries
 