package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val faqList = listOf(
    Pair("Como funciona o UniRun?", "O UniRun é um aplicativo para conectar estudantes universitários que buscam ou oferecem caronas e para facilitar a comunicação entre colegas de curso."),
    Pair("Como posso oferecer uma carona?", "Vá para a seção 'Caronas', toque no botão 'Oferecer carona' e preencha os detalhes da sua carona."),
    Pair("Como posso procurar uma carona?", "Na seção 'Caronas', você pode visualizar as caronas disponíveis."),
    Pair("O aplicativo é gratuito?", "Sim, o UniRun é totalmente gratuito para todos os estudantes."),
    Pair("Como posso entrar em contato com um motorista ou passageiro?", "Após encontrar uma carona de interesse ou um colega na lista de chats, você pode iniciar uma conversa diretamente pelo aplicativo."),
    Pair("Meus dados estão seguros?", "Levamos a privacidade a sério. Seus dados são usados apenas para o funcionamento do aplicativo e não são compartilhados com terceiros sem o seu consentimento. Consulte nossa política de privacidade para mais detalhes."),
    Pair("Como posso editar meu perfil?", "No menu lateral, acesse 'Perfil' para atualizar suas informações, como foto, nome e uma breve biografia.")
)

@Composable
fun FAQScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Perguntas Frequentes (FAQ)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(faqList) { faqItem ->
                    FAQCard(faqItem = faqItem)
                }
            }
        }
    }
}

@Composable
fun FAQCard(faqItem: Pair<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = faqItem.first,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = faqItem.second,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}