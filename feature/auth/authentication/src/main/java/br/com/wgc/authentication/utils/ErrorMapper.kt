package br.com.wgc.authentication.utils

import br.wgc.omnibackend.core.utils.AppError
import br.wgc.omnibackend.core.utils.message

/**
 * Converte erros agnósticos do OmniBackend para mensagens amigáveis para o usuário na camada de apresentação.
 */
fun AppError.toUserFriendlyMessage(): String = this.message
