package nmnb.domain.report

enum class ContentType(val description: String) {
    SEXUAL("성적인 콘텐츠"),
    VIOLENT_OR_HATEFUL("폭력적 또는 혐오스러운 콘텐츠"),
    HATE_OR_MALICIOUS("증오 또는 악의적인 콘텐츠"),
    HARASSMENT_OR_VIOLENCE("괴롭힘 또는 폭력"),
    HARMFUL_OR_DANGEROUS_ACTS("유해하거나 위험한 행위"),
    MISINFORMATION("잘못된 정보"),
    ANIMAL_ABUSE("동물 학대"),
    LEGAL_ISSUES("법적 문제"),
    TERRORISM("테러 조장"),
    SPAM_OR_CHAOTIC("스팸 또는 혼돈을 야기하는 콘텐츠"),
}
