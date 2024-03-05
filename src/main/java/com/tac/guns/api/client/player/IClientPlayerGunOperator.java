package com.tac.guns.api.client.player;

import com.tac.guns.api.gun.ShootResult;
import net.minecraft.client.player.LocalPlayer;

public interface IClientPlayerGunOperator {
    /**
     * 自动检查玩家能否开火，并执行客户端开火逻辑。
     *
     * @return 返回开火的结果，成功或失败。
     */
    ShootResult shoot();

    /**
     * 执行客户端切枪逻辑。
     */
    void draw();

    void reload();

    void inspect();

    void fireSelect();

    void aim(boolean isAim);

    float getClientAimingProgress();

    /**
     * LocalPlayer 通过 Mixin 的方式实现了这个接口
     */
    static IClientPlayerGunOperator fromLocalPlayer(LocalPlayer player) {
        return (IClientPlayerGunOperator) player;
    }
}
