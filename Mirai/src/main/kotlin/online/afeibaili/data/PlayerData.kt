package online.afeibaili.data


/**
 * # 绑定数据
 *
 * @author AfeiBaili
 * @version 2026/9/5 02:34
 */

open class PlayerData {
    constructor()

    constructor(id: Long, avatar: String, mcId: String, mcName: String) : this() {
        this.id = id
        this.avatar = avatar
        this.mcId = mcId
        this.mcName = mcName
    }

    var id = 0L
    var avatar = ""
    var mcId = ""
    var mcName = ""
    var verifyTime = 0L
    var createTime = 0L

    override fun toString(): String {
        return "PlayerData(id=$id, avatar='$avatar', mcId='$mcId', mcName='$mcName', verifyTime=$verifyTime, createTime=$createTime)"
    }

}