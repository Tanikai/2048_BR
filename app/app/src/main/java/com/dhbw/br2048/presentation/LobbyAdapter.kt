package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.data.Lobby
import com.dhbw.br2048.databinding.ItemLobbyBinding

class LobbyAdapter(private var lobbys: MutableList<Lobby>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>(){
    inner class LobbyViewHolder(val b: ItemLobbyBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val b: ItemLobbyBinding =
            ItemLobbyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LobbyViewHolder(b)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val l = lobbys[position]
        holder.b.lobbyID.text = l.id
        holder.b.lobbyOwner.text = l.owner
        @SuppressLint("SetTextI18n")
        holder.b.lobbyPlayerCount.text = "${l.currentUsers} / ${l.maxUsers}"

        holder.itemView.setOnClickListener {
            onClick(l.id)
        }
    }


    override fun getItemCount(): Int {
        return lobbys.size
    }
}