package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.source.local.model.SymbolDB

object SymbolMapper {

    fun toLocalSymbol(
        userId: String,
        symbol: com.bytepace.dimusco.data.model.Symbol
    ): SymbolDB {
        return SymbolDB(userId, symbol.order, symbol.value, symbol.scale)
    }

    fun fromLocalSymbol(symbol: SymbolDB): com.bytepace.dimusco.data.model.Symbol {
        return com.bytepace.dimusco.data.model.Symbol(symbol.order, symbol.value, symbol.scale)
    }
}