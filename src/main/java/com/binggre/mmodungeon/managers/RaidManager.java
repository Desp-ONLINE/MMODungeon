package com.binggre.mmodungeon.managers;

import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.objects.PlayerClearLog;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.repository.DungeonRepository;
import com.binggre.mmodungeon.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RaidManager implements DungeonManager {

    private final PlayerRepository playerRepository;
    private final DungeonRepository dungeonRepository;

    @Override
    public void start(PlayerDungeon playerDungeon, int dungeonId) {
        if (playerDungeon == null) {
            return;
        }

        Player player = playerDungeon.toPlayer();
        Dungeon dungeon = dungeonRepository.get(dungeonId);
        MessageConfig messageConfig = MessageConfig.getInstance();

        // 던전 유효성 체크
        if (!isDungeonValid(player, dungeon, messageConfig)) {
            return;
        }

        // 싱글 검증
        if (!isSingleValid(player, playerDungeon, dungeon, messageConfig)) {
            return;
        }

        // 파티 관련 검증
        Party party = null;
        if (playerDungeon.isJoinParty()) {
            party = playerDungeon.getParty();
            if (!isPartyValid(player, party, dungeon, messageConfig)) {
                return;
            }
        }

        // 던전 방 검색
        DungeonRoom room = dungeon.findEmptyRoom();
        if (room == null) {
            notifyRoomNotFound(playerDungeon, messageConfig);
            return;
        }

        // 플레이어 던전 객체 생성 후 방 참가
        List<PlayerDungeon> playerDungeons = preparePlayerDungeons(playerDungeon);

        if (!callJoinEvent(dungeon, playerDungeons, party)) {
            return;
        }

        room.initJoin();
        room.join(playerDungeons);
    }

    // 메서드: 던전 유효성 체크
    private boolean isDungeonValid(Player player, Dungeon dungeon, MessageConfig message) {
        if (dungeon == null) {
            player.sendMessage(message.getNotFoundDungeon());
            return false;
        }
        if (!dungeon.isEnable()) {
            player.sendMessage(message.getDisableJoin());
            return false;
        }
        if (dungeon.getRooms().isEmpty()) {
            player.sendMessage(message.getNotFoundRoom());
            return false;
        }
        return true;
    }

    // 메서드: 싱글 검증 로직
    private boolean isSingleValid(Player player, PlayerDungeon playerDungeon, Dungeon dungeon, MessageConfig message) {
        PlayerData playerData = PlayerData.get(player);
        Party party = (Party) playerData.getParty();
        if (party != null) {
            return true;
        }
        if (playerData.getLevel() < dungeon.getReqLevel()) {
            player.sendMessage(message.getReqLevelSingle());
            return false;
        }
        PlayerClearLog clearLog = playerDungeon.getClearLog(dungeon.getId());
        return !clearLog.isCooldown(player::sendMessage);
    }

    // 메서드: 파티 검증 로직 (인원수 체크 추가)
    private boolean isPartyValid(Player player, Party party, Dungeon dungeon, MessageConfig message) {
        PlayerData leader = party.getOwner();

        // 최소 및 최대 인원수 가져오기
        int minSize = dungeon.getMinPlayer();
        int maxSize = dungeon.getMaxPlayer();

        // 파티 리더 확인
        if (!leader.getPlayer().equals(player)) {
            player.sendMessage(message.getOnlyPartyLeader());
            return false;
        }

        // 파티 인원수 검증
        int partySize = party.getMembers().size();
        if (partySize < minSize || partySize > maxSize) {
            String playerSizeMessage = message.getNumberOfPeople()
                    .replace("<min>", String.valueOf(minSize))
                    .replace("<max>", String.valueOf(maxSize));
            party.getMembers().forEach(member -> member.getPlayer().sendMessage(playerSizeMessage));
            return false;
        }

        List<PlayerData> onlineMembers = party.getOnlineMembers();

        // 입장 횟수 초과 플레이어 검증
        List<Player> impossibleCountPlayers = getImpossibleCountPlayers(dungeon, onlineMembers);
        if (!impossibleCountPlayers.isEmpty()) {
            notifyImpossiblePlayers(onlineMembers, impossibleCountPlayers, message.getReplyCooldownParty());
            return false;
        }

        // 레벨 요구 미달 플레이어 검증
        List<Player> impossiblePlayers = getImpossiblePlayers(onlineMembers, dungeon.getReqLevel());
        if (!impossiblePlayers.isEmpty()) {
            notifyImpossiblePlayers(onlineMembers, impossiblePlayers, message.getReqLevelParty());
            return false;
        }

        return true;
    }

    // 메서드: 입장 횟수 부족 플레이어 목록 추출
    private List<Player> getImpossibleCountPlayers(Dungeon dungeon, List<PlayerData> members) {
        return members.stream()
                .map(PlayerData::getPlayer)
                .filter(player -> {
                    UUID uuid = player.getUniqueId();
                    PlayerDungeon playerDungeon = playerRepository.get(uuid);
                    PlayerClearLog log = playerDungeon.getClearLog(dungeon.getId());
//                    return dungeon.getMaxJoin() <= log.getCount();
                    return log.isCooldown(String::new);
                })
                .toList();
    }

    // 메서드: 레벨 요구를 충족하지 못한 플레이어 목록 추출
    private List<Player> getImpossiblePlayers(List<PlayerData> members, int reqLevel) {
        return members.stream()
                .filter(member -> member.getLevel() < reqLevel)
                .map(PlayerData::getPlayer)
                .toList();
    }

    // 메서드: 요구 레벨을 충족하지 못한 플레이어에게 알림 보내기
    private void notifyImpossiblePlayers(List<PlayerData> onlineMembers, List<Player> impossiblePlayers, String message) {
        String playerNames = impossiblePlayers.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        String finalMessage = message.replace("<player>", playerNames);
        onlineMembers.stream()
                .map(PlayerData::getPlayer)
                .forEach(member -> member.sendMessage(finalMessage));
    }

    // 메서드: 던전 방이 없을 때 알림
    private void notifyRoomNotFound(PlayerDungeon playerDungeon, MessageConfig message) {
        String notFoundRoom = message.getNotFoundRoom();

        if (!playerDungeon.isJoinParty()) {
            playerDungeon.toPlayer().sendMessage(notFoundRoom);
        } else {
            playerDungeon.getParty().getMembers().forEach(member ->
                    member.getPlayer().sendMessage(notFoundRoom)
            );
        }
    }

    // 메서드: 던전에 참여할 PlayerDungeon 객체 준비
    private List<PlayerDungeon> preparePlayerDungeons(PlayerDungeon playerDungeon) {
        List<PlayerDungeon> playerDungeons = new ArrayList<>();

        if (playerDungeon.isJoinParty()) {
            playerDungeon.getParty().getMembers().forEach(member ->
                    playerDungeons.add(playerRepository.get(member.getUniqueId()))
            );
        } else {
            playerDungeons.add(playerDungeon);
        }

        return playerDungeons;
    }
}